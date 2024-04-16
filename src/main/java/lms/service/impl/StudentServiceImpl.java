package lms.service.impl;

import lms.entities.Student;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StudentServiceImpl {//очуром
    public  static  boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(),"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
    //method for dowload
    public static  List<Student>getStudentsDataFromExcel(InputStream inputStream) throws IOException {
        List<Student>students = new ArrayList<>();

         try {
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
             //students это имя excel file
             XSSFSheet sheet = workbook.getSheet("students");
             int rowIndex = 0;
             for (Row row : sheet){
                 if (rowIndex ==0){
                     rowIndex++;
                     continue;
                 }
                 Iterator<Cell>cellIterator = row.iterator();
             }
         }catch (IOException e){
             e.getStackTrace();
         }
return students;
    }
}
