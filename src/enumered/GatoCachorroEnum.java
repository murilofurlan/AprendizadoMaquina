package enumered;

public enum GatoCachorroEnum {

	GATO(0, "Gato"),
	CACHORRO(1, "Cachorro");
	
	private final double value;
	private final String classe;

	private GatoCachorroEnum(Integer value, String classe) {
		this.value = value;
		this.classe = classe;
	}
	
	public static String getClassePerValue(final double value) {
		for (GatoCachorroEnum gatoCachorro : GatoCachorroEnum.values()) {
			if (gatoCachorro.getValue() == value)
				return gatoCachorro.getClasse();
		}
		return null;
	}
	
	public double getValue() {
		return value;
	}

	public String getClasse() {
		return classe;
	}
}
