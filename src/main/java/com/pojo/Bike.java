package com.pojo;

import org.springframework.stereotype.Repository;

/**
 * distId == bikeIds 
 * distX == lng
 * distY == lat
 * 		"distId": "0296522289",
		"distX": 108.92206478,
		"distY": 34.2703333,
		"distNum": 1,
		"distance": "21",
		"bikeIds": "0296522289#",
		"biketype": 1,
		"type": 0,
		"3": null,
		"operateType": 100
 * @author: daniel
 * @Date: Created in 2018.10.10
 * @Description: Json class of Message object items
 */
@Repository
public class Bike {
	private String distId;
	private String distX;
	private String distY;
	private int distNum;
	private String distance;
	private String bikeIds;
	private int biketype;
	private int type;
	private String boundary;
	private int operateType;

	public Bike() {
		super();
	}

	/**
	 * 获取单车的去掉#号的ID
	 * @return
	 */
	public String getDistId() {
		return distId;
	}
	/**
	 *  获取经度
	 * @return 经度
	 */
	public String getDistX() {
		return distX;
	}
	/**
	 *  获取纬度
	 * @return 纬度
	 */
	public String getDistY() {
		return distY;
	}

	public int getDistNum() {
		return distNum;
	}

	public String getDistance() {
		return distance;
	}

	public String getBikeIds() {
		return bikeIds;
	}

	public int getBiketype() {
		return biketype;
	}

	public int getType() {
		return type;
	}

	public String getBoundary() {
		return boundary;
	}

	public int getOperateType() {
		return operateType;
	}

	public void setDistId(String distId) {
		this.distId = distId;
	}

	public void setDistX(String distX) {
		this.distX = distX;
	}

	public void setDistY(String distY) {
		this.distY = distY;
	}

	public void setDistNum(int distNum) {
		this.distNum = distNum;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void setBikeIds(String bikeIds) {
		this.bikeIds = bikeIds;
	}

	public void setBiketype(int biketype) {
		this.biketype = biketype;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}

	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bikeIds == null) ? 0 : bikeIds.hashCode());
		result = prime * result + biketype;
		result = prime * result
				+ ((boundary == null) ? 0 : boundary.hashCode());
		result = prime * result + ((distId == null) ? 0 : distId.hashCode());
		result = prime * result + distNum;
		result = prime * result + ((distX == null) ? 0 : distX.hashCode());
		result = prime * result + ((distY == null) ? 0 : distY.hashCode());
		result = prime * result
				+ ((distance == null) ? 0 : distance.hashCode());
		result = prime * result + operateType;
		result = prime * result + type;
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
		Bike other = (Bike) obj;
		if (bikeIds == null) {
			if (other.bikeIds != null)
				return false;
		} else if (!bikeIds.equals(other.bikeIds))
			return false;
		if (biketype != other.biketype)
			return false;
		if (boundary == null) {
			if (other.boundary != null)
				return false;
		} else if (!boundary.equals(other.boundary))
			return false;
		if (distId == null) {
			if (other.distId != null)
				return false;
		} else if (!distId.equals(other.distId))
			return false;
		if (distNum != other.distNum)
			return false;
		if (distX == null) {
			if (other.distX != null)
				return false;
		} else if (!distX.equals(other.distX))
			return false;
		if (distY == null) {
			if (other.distY != null)
				return false;
		} else if (!distY.equals(other.distY))
			return false;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.equals(other.distance))
			return false;
		if (operateType != other.operateType)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bike [distId=" + distId + ", distX=" + distX + ", distY="
				+ distY + ", distNum=" + distNum + ", distance=" + distance
				+ ", bikeIds=" + bikeIds + ", biketype=" + biketype + ", type="
				+ type + ", boundary=" + boundary + ", operateType="
				+ operateType + "]";
	}

}
