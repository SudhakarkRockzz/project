package main.GitAutomation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSample {
	
	XSSFWorkbook workbook;
	XSSFSheet sheet;

	public ExcelSample() {
		this.workbook = new XSSFWorkbook();
		this.sheet = workbook.createSheet("sample");
	}
	
	public XSSFRow createRow(int rowNum) {
		XSSFRow row = sheet.createRow(rowNum);
		return row;
	}
		
	public boolean write(int rowNum, int colIndex, String value) {
		
		try {
			XSSFRow row = sheet.getRow(rowNum);
			if(row == null) {
				row = sheet.createRow(rowNum);
			}
			XSSFCell cell = row.createCell(colIndex);	
			cell.setCellValue(value);	
			return true;
		}
		catch(Exception e) {
			return false;
		}
		
	}
	
	
	public boolean saveFile() {
		try {
			FileOutputStream file = new FileOutputStream(new File("/home/sudhakar/Documents/Git_Push_Tracker.xlsx"));
			
			this.workbook.write(file);
			
			file.close();
			
			return true;
		}
		catch (Exception e){
			return false;
		}
				
	}
	
	
	public ArrayList readFile() throws IOException {
		
		ArrayList<ArrayList> array = new ArrayList<ArrayList>();
		
		FileInputStream file = new FileInputStream(new File("/home/sudhakar/Documents/Git_Push_Tracker/Git_Tracker_Excel.xlsx"));
		
		XSSFWorkbook workbook =  new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = sheet.iterator();
		
		if (rowIterator.hasNext()) {
            rowIterator.next(); // Skip the first row
        }
			
		while(rowIterator.hasNext()) {
			
			Row row = rowIterator.next();
			
			Iterator<Cell> cellIterator = row.cellIterator();
			
			ArrayList<String> innerArray = new ArrayList<String>();

			while(cellIterator.hasNext()) {
			    Cell cell = cellIterator.next();
			    innerArray.add(cell.getStringCellValue());
			}
			
			System.out.println(innerArray);
			array.add(innerArray);
		}
	
		file.close();
		return array;
	}
	
}

