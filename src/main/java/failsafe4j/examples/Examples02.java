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
package failsafe4j.examples;

import java.util.UUID;

import failsafe4j.ErrorHandler;
import failsafe4j.Method;
import failsafe4j.FailsafeManager;
import failsafe4j.FailsafeThread;
import failsafe4j.TimeoutHandler;

public class Examples02 {
	
	public Examples02() {
		FailsafeManager failsafeManager = new FailsafeManager();
		failsafeManager.setErrorHandler(new ErrorHandler() {
			@Override
			public void handle(Throwable t, String message, UUID uuid) {
				System.out.println(String.format("ErrorHandler - Exception: %s - %s (UUID=%s)", t.toString(), message, uuid.toString()));
			}
		});
		failsafeManager.setTimeoutHandler(new TimeoutHandler() {
			@Override
			public void handle(String message, UUID uuid) {
				System.out.println(String.format("TimeoutHandler - %s (UUID=%s)", message, uuid.toString()));
			}
		});
		
		Method method = new Method() {
			@Override
			public void run(UUID uuid) {
				/*
				@SuppressWarnings("unused")
				int z = 67/0;
				*/
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void error(Throwable t) {
				// System.out.println(e.getMessage());
			}
			
			@Override
			public void after() {
				System.out.println("Hello World!");
			}
		};
		
		FailsafeThread.run(failsafeManager, "Methode 1", method, UUID.randomUUID(), 1000);
		
		System.out.println("YES!");
	}
	
	public static void main(String[] args) {
		new Examples02();
	}
}
