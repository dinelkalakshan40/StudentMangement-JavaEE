package lk.ijse.test2.controllers;

import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.test2.dto.StudentDTO;
import lk.ijse.test2.dao.StudentDataProcess;
import lk.ijse.test2.util.UtillProcess;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/student")
/*@WebServlet(urlPatterns = "/student",
        initParams = {
                @WebInitParam(name = "driver-class",value = "com.mysql.cj.jdbc.Driver"),
                @WebInitParam(name = "dbURL",value = "jdbc:mysql://localhost:3306/aad67JavaEE?createDatabaseIfNotExist=true"),
                @WebInitParam(name = "dbUserName",value = "root"),
                @WebInitParam(name = "dbPassword",value = "12345"),
        }
)*/
public class StudentController extends HttpServlet {
    Connection connection;

    static String SAVE_STUDENT="INSERT INTO student (id,name,city,email,level) VALUES (?,?,?,?,?)";
    static String Get_STUDENT="SELECT * from student where id=?";
    static String UPDATE_STUDENT = "UPDATE student SET name=?,city=?,email=?,level=? WHERE id=?";
    static String DELETE_SUTNDET ="DELETE FROM student WHERE id=?";
    @Override
    public void init() throws ServletException{
        try {
            var driverClass =getServletContext().getInitParameter("driver-class");
            var dbUrl =getServletContext().getInitParameter("dbURL");
            var UserName =getServletContext().getInitParameter("dbUserName");
            var Password =getServletContext().getInitParameter("dbPassword");
            Class.forName(driverClass);
            this.connection= DriverManager.getConnection(dbUrl,UserName,Password);
        }catch (ClassNotFoundException | SQLException e){
            throw new RuntimeException(e);
        }
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        if (!req.getContentType().toLowerCase().startsWith("application/json")) {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);

        }
        /*String id = UUID.randomUUID().toString();
        Jsonb jsonb = JsonbBuilder.create();
        StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
        studentDTO.setId(id);
        System.out.println(studentDTO);*/

        /* Jsonb jsonb  =JsonbBuilder.create();
        List<StudentDTO> studentDTOList= jsonb.fromJson(req.getReader(), new ArrayList<StudentDTO>(){
        }.getClass().getGenericSuperclass());
        studentDTOList.forEach(System.out::println);*/



        try(var writer =response.getWriter()) {
            Jsonb jsonb =JsonbBuilder.create();
            StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
            studentDTO.setId(UtillProcess.generateId());
            /*var saveData = new StudentDataProcess();
            writer.write(saveData.saveStudent(studentDTO, connection));*/
            var saveData = new StudentDataProcess();
            if (saveData.saveStudent(studentDTO, connection)){
                writer.write("Student saved successfully");
                response.setStatus(HttpServletResponse.SC_CREATED);
            }else {
                writer.write("Save student failed");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);

            }
        } catch (JsonException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);


        }
    }
    @Override
    protected void doGet(HttpServletRequest req,HttpServletResponse response) throws ServletException,IOException{
       // var studentDto= new StudentDTO();
        var studentId = req.getParameter("id");
        var dataProcess = new StudentDataProcess();
        System.out.println("Received student ID: " + studentId);

        try (var writer= response.getWriter()){
            var student = dataProcess.getStudent(studentId,connection);
            System.out.println(student);
            response.setContentType("application/json");
            var jsonb =JsonbBuilder.create();
           // jsonb.toJson(studentDto,response.getWriter());
            jsonb.toJson(student ,writer);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req,HttpServletResponse response) throws  IOException {
        //Todo:Update student
        if(!req.getContentType().toLowerCase().startsWith("application/json")|| req.getContentType() == null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        try (var writer = response.getWriter()){

            var studentID=req.getParameter("stu-id");
            Jsonb jsonb = JsonbBuilder.create();
            var studentDataProcess = new StudentDataProcess();
            var updateStudent = jsonb.fromJson(req.getReader(), StudentDTO.class);

            if(studentDataProcess.updateStudent(studentID,updateStudent,connection)){
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
              //  writer.write("Student Updated");
            }else {
                writer.write("Update Failed");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (JsonException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req ,HttpServletResponse response) throws IOException {
        var stuId = req.getParameter("stu-id");
        try (var writer = response.getWriter()){
            var ps = this.connection.prepareStatement(DELETE_SUTNDET);

            var studentDataProcess = new StudentDataProcess();
            if(studentDataProcess.deleteStudent(stuId, connection)){
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("Delete Failed");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}
