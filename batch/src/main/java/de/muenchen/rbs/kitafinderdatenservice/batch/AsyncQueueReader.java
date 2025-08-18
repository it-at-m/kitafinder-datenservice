package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.core.task.TaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * Reads data in a separate, single thread and buffers it in a
 * {@link BlockingQueue} to guarantee quick returns for read(). This is
 * especially helpful in combination with a {@link SynchronizedItemStreamReader}
 * and multiple threads.
 * 
 * Implementing classes need to provide their own data-loading logic in
 * getNextBatch().
 * 
 * @param <T> Class of the Items this reader is providing.
 */
@Slf4j
public abstract class AsyncQueueReader<T> implements ItemStreamReader<T>, ItemStream {

	private BlockingQueue<T> dataQueue;

	private boolean done = false;

	private TaskExecutor taskExecutor;

	public AsyncQueueReader(int queueSize, TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
		dataQueue = new ArrayBlockingQueue<>(queueSize);
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		taskExecutor.execute(() -> {
			while (!done) {
				try {
					this.loadNextBatch();
				} catch (InterruptedException e) {
					throw new RuntimeException("Interrupted during data loading: " + e.getMessage());
				}
			}
		});
	}

	@Override
	public T read()
			throws UnexpectedInputException, ParseException, NonTransientResourceException, InterruptedException {
		if (done && dataQueue.size() == 0) {
			return null;
		}
		// the timeout ensures the batch stops after everything is done in all cases
		T next = dataQueue.poll(60, TimeUnit.SECONDS);
		return next;
	}

	private void loadNextBatch() throws InterruptedException {
		for (T nextToAdd : getNextBatch()) {
			if (nextToAdd == null) {
				done = true;
			} else {
				dataQueue.put(nextToAdd);
			}
		}
	}

	/**
	 * Get the next batch of elements. <code>null</code> marks the end of the input.
	 * 
	 * @return next list of elements
	 */
	protected abstract List<T> getNextBatch();

}
