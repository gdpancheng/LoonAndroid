package com.android.pc.ioc.a.demo;

import java.util.List;

import com.android.pc.ioc.db.annotation.Finder;
import com.android.pc.ioc.db.annotation.NoAutoIncrement;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-1-23
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class User {
	
	protected int id;
	protected String content;
	protected long time;
	protected String describe;
	protected boolean is_answer;
	protected String type;
	protected int answer;
	protected int correct;
	protected int server_id;
	protected boolean answer_result;

	@Finder(valueColumn = "id", targetColumn = "UserId")
	private List<User2> yuAnswerEntities;
	
	public int getServer_id() {
		return server_id;
	}

	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", content=" + content + ", time=" + time + ", describe=" + describe + ", is_answer=" + is_answer + ", type=" + type + ", answer=" + answer + ", correct=" + correct + ", server_id=" + server_id + ", answer_result=" + answer_result + ", yuAnswerEntities=" + yuAnswerEntities + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public boolean isIs_answer() {
		return is_answer;
	}

	public void setIs_answer(boolean is_answer) {
		this.is_answer = is_answer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public boolean isAnswer_result() {
		return answer_result;
	}

	public void setAnswer_result(boolean answer_result) {
		this.answer_result = answer_result;
	}

	public List<User2> getYuAnswerEntities() {
		return yuAnswerEntities;
	}

	public void setYuAnswerEntities(List<User2> yuAnswerEntities) {
		this.yuAnswerEntities = yuAnswerEntities;
	}
}
