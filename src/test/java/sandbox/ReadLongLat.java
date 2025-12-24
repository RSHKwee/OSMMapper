package sandbox;

import java.util.ArrayList;

import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.ReadOSMMapExcel;

public class ReadLongLat {
  public static void main(String[] args) throws Exception {
    ArrayList<MemoContent> memocontarr = new ArrayList<MemoContent>();
    String inpFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst.xlsx";

    memocontarr = ReadOSMMapExcel.ReadExcel(inpFile);
    System.out.println();

    inpFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\hoevelaken-warmtescan.xlsx";
    memocontarr = ReadOSMMapExcel.ReadExcel(inpFile);
    System.out.println();

    inpFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten.xlsx";
    memocontarr = ReadOSMMapExcel.ReadExcel(inpFile);
    System.out.println();

  }

}
