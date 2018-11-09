package com.pojo;

import java.util.Arrays;

/**
 * @author: daniel
 * @Date: Created in 2018.10.10
 * @Description: Json class of page content
 *
 */
public class Message {
	private int code;
	private String message;
	private int biketype;
	private boolean autoZoom;
	private int radius;
	private Bike object[];
	private int hasRedPacket;
	
	public Message() {
		super();
	}
	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public int getBiketype() {
		return biketype;
	}
	public boolean isAutoZoom() {
		return autoZoom;
	}
	public int getRadius() {
		return radius;
	}
	public Bike[] getObject() {
		return object;
	}
	public int getHasRedPacket() {
		return hasRedPacket;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setBiketype(int biketype) {
		this.biketype = biketype;
	}
	public void setAutoZoom(boolean autoZoom) {
		this.autoZoom = autoZoom;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public void setObject(Bike[] object) {
		this.object = object;
	}
	public void setHasRedPacket(int hasRedPacket) {
		this.hasRedPacket = hasRedPacket;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (autoZoom ? 1231 : 1237);
		result = prime * result + biketype;
		result = prime * result + code;
		result = prime * result + hasRedPacket;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + Arrays.hashCode(object);
		result = prime * result + radius;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (autoZoom != other.autoZoom)
			return false;
		if (biketype != other.biketype)
			return false;
		if (code != other.code)
			return false;
		if (hasRedPacket != other.hasRedPacket)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (!Arrays.equals(object, other.object))
			return false;
		if (radius != other.radius)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Message [code=" + code + ", message=" + message + ", biketype="
				+ biketype + ", autoZoom=" + autoZoom + ", radius=" + radius
				+ ", object=" + Arrays.toString(object) + ", hasRedPacket="
				+ hasRedPacket + "]";
	}
	

}
