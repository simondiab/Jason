package jason;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jason.model.ProcessedEvent;
import jason.model.RawEvent;

public class JSONProcessor {
	private static final Logger logger = LoggerFactory.getLogger(JSONProcessor.class);
	
	public static void processJsonEvent(RawEvent rawEvent) {
		logger.info("Begin processing of event: " + rawEvent.getId());
		// Attempt to find another event of the same ID.
		// If we find another event then we can be sure that we have an event that has a
		// start timestamp and an end timestamp.
		RawEvent matchedEvent = JSONDbUtils.getStartEvent(rawEvent.getId());
		if (matchedEvent != null) {

			ProcessedEvent processedEvent = new ProcessedEvent();
			processedEvent.setId(rawEvent.getId());
			processedEvent.setType(rawEvent.getType());
			processedEvent.setHost(rawEvent.getHost());

			// We don't know if the matched event is the 'STARTED' or 'FINISHED' event, so
			// try calcualte a duration until the number is above zero
			long duration = rawEvent.getTimestamp() - matchedEvent.getTimestamp();
			if (duration < 0) {
				duration = matchedEvent.getTimestamp() - rawEvent.getTimestamp();
			}

			processedEvent.setDuration(duration);

			if (duration > 4) {
				processedEvent.setAlert(true);
			}

			JSONDbUtils.insertProcessedEvent(processedEvent);
		}
		JSONDbUtils.insertRawEvent(rawEvent);
		logger.info(rawEvent.toString());
	}
}
