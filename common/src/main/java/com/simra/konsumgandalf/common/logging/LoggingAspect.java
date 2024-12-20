package com.simra.konsumgandalf.common.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Aspect
@Component
public class LoggingAspect {

	public static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	private final ConcurrentMap<String, StopWatch> stopWatches = new ConcurrentHashMap<>();

	private final ConcurrentMap<String, Long> totalTimes = new ConcurrentHashMap<>();

	@Around("@annotation(com.simra.konsumgandalf.common.logging.LogExecutionTime)")
	public Object methodTimeLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();
		String key = className + "->" + methodName;

		StopWatch stopWatch = stopWatches.computeIfAbsent(key, k -> new StopWatch(k));
		stopWatch.start(methodName);
		Object result = proceedingJoinPoint.proceed();
		stopWatch.stop();

		totalTimes.merge(key, stopWatch.getLastTaskTimeMillis(), Long::sum);

		return result;
	}

	@Scheduled(cron = "5 * * * * *")
	public void printAllStopWatches() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------------------------------------\n");
		sb.append("Seconds       %       Task name\n");
		sb.append("------------------------------------------------------------------------\n");

		long totalTime = totalTimes.values().stream().mapToLong(Long::longValue).sum();

		totalTimes.keySet().stream().sorted().forEach(key -> {
			long time = totalTimes.get(key);
			double timeSeconds = time / 1000.0;
			int percentage = (int) ((time * 100.0) / totalTime);
			sb.append(String.format("%-13.4f %-8d %-30s\n", timeSeconds, percentage, key));
		});

		if (logger.isInfoEnabled()) {
			logger.info(sb.toString());
		}
	}

}
