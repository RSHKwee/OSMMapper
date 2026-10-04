package kwee.osmmapper.report;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Pure logica voor het selecteren van rapporttypes.
 * Geen Swing, geen I/O — volledig unit-testbaar.
 */
public class ReportSelectionModel {

  private final Map<ReportType, Boolean> selection = new EnumMap<>(ReportType.class);
  private boolean cancelled = false;

  /**
   * @param voorselectie types die standaard aangevinkt moeten zijn;
   *                     null betekent: alles aangevinkt.
   */
  public ReportSelectionModel(Set<ReportType> voorselectie) {
    for (ReportType type : ReportType.values()) {
      boolean selected = (voorselectie == null) || voorselectie.contains(type);
      selection.put(type, selected);
    }
  }

  public boolean isSelected(ReportType type) {
    return Boolean.TRUE.equals(selection.get(type));
  }

  public void setSelected(ReportType type, boolean selected) {
    selection.put(type, selected);
  }

  public void toggle(ReportType type) {
    setSelected(type, !isSelected(type));
  }

  public void selectAll() {
    selection.replaceAll((k, v) -> true);
  }

  public void clearAll() {
    selection.replaceAll((k, v) -> false);
  }

  /** Markeer de dialoog als geannuleerd; resultaat wordt dan null. */
  public void cancel() {
    this.cancelled = true;
  }

  /**
   * @return de geselecteerde types, of null als geannuleerd.
   */
  public Set<ReportType> getResult() {
    if (cancelled) {
      return null;
    }
    Set<ReportType> result = EnumSet.noneOf(ReportType.class);
    selection.forEach((type, selected) -> {
      if (Boolean.TRUE.equals(selected)) {
        result.add(type);
      }
    });
    return result;
  }

  /** Alleen-lezen view, handig voor tests. */
  public Map<ReportType, Boolean> asMap() {
    return Collections.unmodifiableMap(selection);
  }
}