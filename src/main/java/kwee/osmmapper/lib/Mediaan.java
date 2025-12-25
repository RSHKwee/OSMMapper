package kwee.osmmapper.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mediaan {

  public static double mediaanList(List<Double> lijst) {
    if (lijst == null || lijst.isEmpty()) {
      return Double.NaN;
    }

    return lijst.stream().sorted().skip((lijst.size() - 1) / 2L).limit(2 - lijst.size() % 2)
        .mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
  }

  // Alternatieve eenvoudige versie
  public static double mediaanEenvoudig(List<Double> lijst) {
    if (lijst == null || lijst.isEmpty()) {
      return Double.NaN;
    }

    List<Double> gesorteerd = new ArrayList<>(lijst);
    Collections.sort(gesorteerd);

    int n = gesorteerd.size();

    if (n % 2 == 1) {
      return gesorteerd.get(n / 2);
    } else {
      return (gesorteerd.get(n / 2 - 1) + gesorteerd.get(n / 2)) / 2.0;
    }
  }
}
