/*
 * failsafe4j - Failsafe Library
 * Copyright (c) 2014-2020, David A. Bauer
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package failsafe4j;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import failsafe4j.utils.TimeoutTimer;
import failsafe4j.utils.TimeoutTimerListener;

public final class FailsafeThread {
	public static boolean run(final FailsafeManager failsafeManager, final String message, final Method method, final UUID uuid, int timeout) {
		return run(failsafeManager, message, method, uuid, timeout, false);
	}
	
	public static boolean runAndCatchThrowable(final FailsafeManager failsafeManager, final String message, final Method method, final UUID uuid, int timeout) {
		return run(failsafeManager, message, method, uuid, timeout, true);
	}
	
	protected static boolean run(final FailsafeManager failsafeManager, final String message, final Method method, final UUID uuid, int timeout, boolean catchThrowable) {
		final AtomicBoolean result = new AtomicBoolean(true);
		
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (!catchThrowable)
					FailsafeMethod.run(failsafeManager, message, method, uuid);
				else
					FailsafeMethod.runAndCatchThrowable(failsafeManager, message, method, uuid);
			}
		});
		
		TimeoutTimer timeoutTimer = new TimeoutTimer(timeout, new TimeoutTimerListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void task() {
				result.set(false);
				
				if (message!=null)
					System.out.printf("Method failed (timeout): %s (UUID: %s)%n", message, uuid.toString());
				
				failsafeManager.notifyTimeoutHandler(message, uuid);
				
				thread.stop();
			}
		});
		
		timeoutTimer.start();
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timeoutTimer.interrupt();
		
		return result.get();
	}
	
	public static void run(final FailsafeManager failsafeManager, final Method method, final UUID uuid, int timeout) {
		run(failsafeManager, null, method, uuid, timeout);
	}
	
	public static void runAndCatchThrowable(final FailsafeManager failsafeManager, final Method method, final UUID uuid, int timeout) {
		runAndCatchThrowable(failsafeManager, null, method, uuid, timeout);
	}
}
