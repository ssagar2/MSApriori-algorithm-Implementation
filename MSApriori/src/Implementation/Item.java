package Implementation;



public class Item {

	private String itemName;
	private Double MIS;
	private Double actualSupport;
	private int itemCount;

	public Item() {
		super();
	}

	public Item(String itemName,int itemCount, Double MIS, Double actualSupport) {
		super();
		this.itemName = itemName;
		this.itemCount = itemCount;
		this.MIS = MIS;
		this.actualSupport = actualSupport;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getMIS() {
		return MIS;
	}

	public void setMIS(Double MIS) {
		this.MIS = MIS;
	}

	public Double getactualSupport() {
		return actualSupport;
	}

	public void setactualSupport(Double actualSupport) {
		this.actualSupport = actualSupport;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
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
		Item other = (Item) obj;
		if (itemName == null) {
			if (other.itemName != null)
				return false;
		} else if (!itemName.equals(other.itemName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getItemName();
	}
}
