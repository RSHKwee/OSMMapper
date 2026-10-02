package kwee.osmmapper.report;

public enum ReportType {
  POSTCODE_PER_STRAATKANT("Foto's per postcode en straatkant"),
  POSTCODE_EENVOUDIG    ("Foto's per postcode (eenvoudig)"),
  MEMO_OVERZICHT        ("Memo overzicht (uitgangsdata)");

  private final String label;

  ReportType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}