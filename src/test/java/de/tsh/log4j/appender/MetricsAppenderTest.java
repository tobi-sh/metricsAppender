package de.tsh.log4j.appender;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;

public class MetricsAppenderTest {
	
	@Test
	public void testMetersWillBeIncreasedWhenLoggingOnAcertainLogLevel() {
		Logger log = Logger.getLogger(MetricsAppenderTest.class);
		log.addAppender(new MetricsAppender("foo"));
		log.debug("debug");
		log.info("info");
		log.warn("warn");
		log.error("error");
		log.fatal("FATAL");
		
		MetricName debugMetricName = new MetricName("foo", "logger", "debug");
		MetricName infoMeterName = new MetricName("foo", "logger", "info");
		MetricName warnMetricName = new MetricName("foo", "logger", "warn");
		MetricName errorMetricName = new MetricName("foo", "logger", "error");
		MetricName fatalMetricName = new MetricName("foo", "logger", "fatal");
		
		Meter infoMetric = (Meter) Metrics.defaultRegistry().allMetrics().get(infoMeterName);
		Meter debugMetric = (Meter) Metrics.defaultRegistry().allMetrics().get(debugMetricName);
		Meter warnMetric = (Meter) Metrics.defaultRegistry().allMetrics().get(warnMetricName);
		Meter errorMetric = (Meter) Metrics.defaultRegistry().allMetrics().get(errorMetricName);
		Meter fatalMetric = (Meter) Metrics.defaultRegistry().allMetrics().get(fatalMetricName);
		
		assertThat( infoMetric.count(), CoreMatchers.is(1L));
		assertThat( debugMetric.count(), CoreMatchers.is(1L));
		assertThat( warnMetric.count(), CoreMatchers.is(1L));
		assertThat( errorMetric.count(), CoreMatchers.is(1L));
		assertThat( fatalMetric.count(), CoreMatchers.is(1L));
	}
	
	@Test
	public void testAppenderWillRegisterMetricsForEachLogLevel() {
		MetricsAppender metricsAppender = new MetricsAppender("foo");
		
		MetricName expectDebugMetricName = new MetricName("foo", "logger", "debug");
		MetricName expectInfoMetricName = new MetricName("foo", "logger", "info");
		MetricName expectWarnMetricName = new MetricName("foo", "logger", "warn");
		MetricName expectErrorMetricName = new MetricName("foo", "logger", "error");
		MetricName expectFatalMetricName = new MetricName("foo", "logger", "fatal");
		
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				hasItem(expectInfoMetricName) );
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				hasItem(expectDebugMetricName) );
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				hasItem(expectWarnMetricName) );
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				hasItem(expectErrorMetricName) );
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				hasItem(expectFatalMetricName) );
	}
	
	@Test
	public void testNewMetersWillBeRegisteredIfBaseNameChanges() {
		MetricsAppender metricsAppender = new MetricsAppender("foo");
		
		MetricName expectInfoMetricName = new MetricName("foo", "logger", "info");
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				hasItem(expectInfoMetricName) );
		
		metricsAppender.setMetricBaseName("bar");
		
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				CoreMatchers.not( hasItem(expectInfoMetricName) ));
		
		MetricName expectBarInfoMetricName = new MetricName("foo", "logger", "info");
		assertThat( Metrics.defaultRegistry().allMetrics().keySet(),
				CoreMatchers.not( hasItem(expectBarInfoMetricName) ));
	}

}
