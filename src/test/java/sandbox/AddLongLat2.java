package sandbox;

import java.util.ArrayList;

import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.OSMMapExcel;

public class AddLongLat2 {
  public static void main(String[] args) throws Exception {
    ArrayList<MemoContent> memocontarr = new ArrayList<MemoContent>();
    String inpFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst.xlsx";

    OSMMapExcel mexcel1 = new OSMMapExcel(inpFile);
    memocontarr = mexcel1.ReadExcel();
    System.out.println();

    inpFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\hoevelaken-warmtescan.xlsx";
    OSMMapExcel mexcel2 = new OSMMapExcel(inpFile);
    memocontarr = mexcel2.ReadExcel();
    System.out.println();

    inpFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten.xlsx";
    OSMMapExcel mexcel3 = new OSMMapExcel(inpFile);
    memocontarr = mexcel3.ReadExcel();
    System.out.println();

  }

}
