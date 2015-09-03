package de.tsh.log4j.appender;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;

public class MetricsAppender implements Appender {

	private ErrorHandler errorHandler;
	
	private String name;
	
	protected String metricBaseName;
	
	private Meter infoMeter;
	
	private Meter debugMeter;
	
	private Meter warnMeter;
	
	private Meter errorMeter;
	
	private Meter fatalMeter;
	
	public MetricsAppender() {
		this("");
	}

	public MetricsAppender(String metricBaseName) {
		setMetricBaseName(metricBaseName);
	}
	
	@Override
	public void addFilter(Filter newFilter) {
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	@Override
	public void clearFilters() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doAppend(LoggingEvent event) {
		if (event.getLevel().equals(Level.DEBUG)) {
			debugMeter.mark();
		}
		else if (event.getLevel().equals(Level.INFO)) {
			infoMeter.mark();
		}
		else if (event.getLevel().equals(Level.WARN)) {
			warnMeter.mark();
		}
		else if (event.getLevel().equals(Level.ERROR)) {
			errorMeter.mark();
		}
		else if (event.getLevel().equals(Level.FATAL)) {
			fatalMeter.mark();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;

	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public void setLayout(Layout layout) {
	}

	@Override
	public Layout getLayout() {
		return null;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	public String getMetricBaseName() {
		return metricBaseName;
	}

	public void setMetricBaseName(String metricBaseName) {
		deregisterPreviousMeters(this.metricBaseName);
		createMeters(metricBaseName);
		this.metricBaseName = metricBaseName;
	}

	private void deregisterPreviousMeters(String baseName) {
		if (null == baseName) {
			return;
		}
		
		Metrics.defaultRegistry().removeMetric(new MetricName(metricBaseName, "logger", "debug"));
		Metrics.defaultRegistry().removeMetric(new MetricName(metricBaseName, "logger", "info"));
		Metrics.defaultRegistry().removeMetric(new MetricName(metricBaseName, "logger", "warn"));
		Metrics.defaultRegistry().removeMetric(new MetricName(metricBaseName, "logger", "error"));
		Metrics.defaultRegistry().removeMetric(new MetricName(metricBaseName, "logger", "fatal"));
	}
	
	private void createMeters(String metricBaseName) {
		this.infoMeter = Metrics.newMeter(new MetricName(metricBaseName, "logger", "info"), "LOGS", TimeUnit.SECONDS);
		this.debugMeter = Metrics.newMeter(new MetricName(metricBaseName, "logger", "debug"), "LOGS", TimeUnit.SECONDS);
		this.warnMeter = Metrics.newMeter(new MetricName(metricBaseName, "logger", "warn"), "LOGS", TimeUnit.SECONDS);
		this.errorMeter = Metrics.newMeter(new MetricName(metricBaseName, "logger", "error"), "LOGS", TimeUnit.SECONDS);
		this.fatalMeter = Metrics.newMeter(new MetricName(metricBaseName, "logger", "fatal"), "LOGS", TimeUnit.SECONDS);
	}

}
