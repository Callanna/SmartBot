package com.ebanswers.smartlib.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2016/1/12.
 */
public class IFlyJsonResult {
	private String confidence;// 置信度
	public List<String> slots = new ArrayList<String>();//词槽
	public List<String> words = new ArrayList<String>();//词槽对应的 词语

	
	public IFlyJsonResult() {
		super();
	}

	public String getConfidence() {
		return confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public List<String> getSlots() {
		return slots;
	}

	public void addSlots(String slots) {
		this.slots.add(slots);
	}

	public List<String> getWords() {
		return words;
	}

	public void addWords(String words) {
		this.words.add(words);
	}
}
