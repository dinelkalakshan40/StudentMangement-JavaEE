package lk.ijse.test2.dao;

import lk.ijse.test2.dto.StudentDTO;

import java.sql.Connection;
import java.sql.SQLException;

public interface StudentData  {
    StudentDTO getStudent(String studentId, Connection connection) throws SQLException;
    boolean saveStudent(StudentDTO studentDTO,Connection connection);
    boolean deleteStudent(String studentId,Connection connection);
    boolean updateStudent(String studentId, StudentDTO updateStudent, Connection connection);
}
