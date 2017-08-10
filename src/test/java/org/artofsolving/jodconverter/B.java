package org.artofsolving.jodconverter;

import java.util.concurrent.atomic.AtomicLong;

public class B {

	public static void main(String[] args) {
		AtomicLong counter = new AtomicLong(1);
		System.out.println(Long.MAX_VALUE); //9223372036854775807
		for(int i=0;i<10;i++)
			System.out.println(counter.getAndIncrement());
		
	}

}
