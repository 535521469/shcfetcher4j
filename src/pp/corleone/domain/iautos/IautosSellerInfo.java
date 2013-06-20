package pp.corleone.domain.iautos;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import pp.corleone.domain.SellerInfo;

public class IautosSellerInfo extends SellerInfo implements Cloneable,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8254284734895259457L;

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("phone", this.getShopPhone())
				.append("address", this.getShopAddress())
				.append("name", this.getShopName()).toString();
	}
}
