WITH cte AS (
    SELECT MIN(ctid) AS keep_ctid, osm_id
    FROM planet_osm_line
    GROUP BY osm_id
    HAVING COUNT(*) > 1
)
DELETE FROM planet_osm_line
WHERE osm_id IN (SELECT osm_id FROM cte)
  AND ctid NOT IN (SELECT keep_ctid FROM cte);

DO
'
DECLARE
BEGIN
    -- Check if the primary key constraint already exists
    IF NOT EXISTS (
        SELECT 1
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu
                ON tc.constraint_name = kcu.constraint_name
                WHERE tc.table_name = ''planet_osm_line''
                AND tc.constraint_type = ''PRIMARY KEY''
                AND kcu.column_name = ''osm_id''
    ) THEN
        ALTER TABLE planet_osm_line ADD UNIQUE (osm_id);
        ALTER TABLE planet_osm_line ADD PRIMARY KEY (osm_id);
    END IF;
END;
'  LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION calculate_geometry_and_clear_coordinates()
RETURNS TRIGGER AS '
BEGIN
    -- Convert the coordinates JSON into a POINT geometry
    WITH points AS (
        SELECT ST_SetSRID(ST_MakePoint(coord.lng, coord.lat), 4326) AS geom
        FROM json_populate_recordset(NULL::record, NEW.coordinates::json) AS coord(lng double precision, lat double precision)
    )
    -- Create the LINESTRING geometry from the points
    UPDATE ride_entity
    SET way = (SELECT ST_MakeLine(geom) FROM points),
        coordinates = NULL  -- Clear the coordinates field
    WHERE id = NEW.id;

    -- Return the NEW record
    RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trigger_calculate_geometry_and_clear_coordinates
AFTER INSERT ON ride_entity
FOR EACH ROW
EXECUTE FUNCTION calculate_geometry_and_clear_coordinates();

-- Auto calculate the way of all ride incidents
CREATE OR REPLACE FUNCTION calculate_way_of_ride_incident()
RETURNS TRIGGER AS '
BEGIN
    NEW.way = ST_MakePoint(NEW.lng, NEW.lat);
    RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trigger_calculate_way_of_ride_incident
BEFORE INSERT OR UPDATE ON ride_incident
FOR EACH ROW
EXECUTE FUNCTION calculate_way_of_ride_incident();
