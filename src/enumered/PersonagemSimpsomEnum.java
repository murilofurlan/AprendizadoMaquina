package enumered;

public enum PersonagemSimpsomEnum {

	NED(0, "Ned"),
	MILHOUSE(1, "Milhouse");
	
	private final double value;
	private final String classe;

	private PersonagemSimpsomEnum(Integer value, String classe) {
		this.value = value;
		this.classe = classe;
	}
	
	public static String getClassePerValue(final double value) {
		for (PersonagemSimpsomEnum personagem : PersonagemSimpsomEnum.values()) {
			if (personagem.getValue() == value)
				return personagem.getClasse();
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
