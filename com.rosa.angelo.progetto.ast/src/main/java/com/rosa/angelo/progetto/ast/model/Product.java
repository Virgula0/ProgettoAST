package com.rosa.angelo.progetto.ast.model;

import java.util.Objects;

public class Product {
	private int id;
	private User sender;
	private String receiverName;
	private String receiverSurname;
	private String reiceiverAddress;
	private String packageType;

	public Product(User sender, String receiverName, String receiverSurname, String reiceiverAddress,
			String packageType) {
		this.sender = sender;
		this.receiverName = receiverName;
		this.receiverSurname = receiverSurname;
		this.reiceiverAddress = reiceiverAddress;
		this.packageType = packageType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, packageType, receiverName, receiverSurname, reiceiverAddress, sender);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Product other = (Product) obj;
		return id == other.id && Objects.equals(packageType, other.packageType)
				&& Objects.equals(receiverName, other.receiverName)
				&& Objects.equals(receiverSurname, other.receiverSurname)
				&& Objects.equals(reiceiverAddress, other.reiceiverAddress) && Objects.equals(sender, other.sender);
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", sender=" + sender + ", receiverName=" + receiverName + ", receiverSurname="
				+ receiverSurname + ", reiceiverAddress=" + reiceiverAddress + ", packageType=" + packageType + "]";
	}

}
