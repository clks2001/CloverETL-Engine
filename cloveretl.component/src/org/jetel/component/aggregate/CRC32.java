/**
 * 
 */
package org.jetel.component.aggregate;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.jetel.data.DataField;
import org.jetel.data.DataRecord;
import org.jetel.data.Defaults;
import org.jetel.metadata.DataFieldMetadata;

/**
 * Calculates CRC32 on an aggregation group.
 * 
 * Output field must be Long and nullable if input is nullable.
 * 
 * @author Jaroslav Urban
 *
 */
public class CRC32 extends AggregateFunction {
	private static final String NAME = "CRC32";
	
	private int loopCount = 0;
	private java.util.zip.CRC32 crc32;
	ByteBuffer dataBuffer = ByteBuffer.allocate(Defaults.DEFAULT_INTERNAL_IO_BUFFER_SIZE);
	private CharsetEncoder encoder;

	// Is input nullable?
	private boolean nullableInput;


	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#checkInputFieldType(org.jetel.metadata.DataFieldMetadata)
	 */
	@Override
	public void checkInputFieldType(DataFieldMetadata inputField) throws AggregateProcessorException {
		nullableInput = inputField.isNullable();
		return;
	}

	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#checkOutputFieldType(org.jetel.metadata.DataFieldMetadata)
	 */
	@Override
	public void checkOutputFieldType(DataFieldMetadata outputField) throws AggregateProcessorException {
		if (nullableInput && !outputField.isNullable()) {
			throw new AggregateProcessorException(AggregateFunction.ERROR_NULLABLE_BECAUSE_INPUT);
		}
		if (outputField.getType() != DataFieldMetadata.LONG_FIELD) {
			throw new AggregateProcessorException(AggregateFunction.ERROR_LONG);
		}
	}

	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#init()
	 */
	@Override
	public void init() {
		crc32 = new java.util.zip.CRC32();
		crc32.reset();
		
        if(charset == null) {
            encoder = Charset.forName(Defaults.DataFormatter.DEFAULT_CHARSET_ENCODER).newEncoder();
        } else {
            encoder = Charset.forName(charset).newEncoder();
        }
	}

	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#requiresInputField()
	 */
	@Override
	public boolean requiresInputField() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#storeResult(org.jetel.data.DataField)
	 */
	@Override
	public void storeResult(DataField outputField) {
		if (loopCount == 0) {
			outputField.setNull(true);
		} else {
			outputField.setValue(crc32.getValue());
		}
	}

	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#update(org.jetel.data.DataRecord)
	 */
	@Override
	public void update(DataRecord record) throws Exception {
		DataField input = record.getField(inputFieldIndex);
		if (input.isNull()) {
			return;
		}
		dataBuffer.clear();
		input.toByteBuffer(dataBuffer, encoder);
		crc32.update(dataBuffer.array());
		loopCount++;
	}

	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see org.jetel.component.aggregate.AggregateFunction#clear()
	 */
	@Override
	public void clear() {
		crc32.reset();
	}
}
