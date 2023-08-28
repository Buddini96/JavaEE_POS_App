package lk.ijse.pos.servlet;

import lk.ijse.pos.dto.CustomerDTO;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = {"/pages/customer"})
public class CustomerServletAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("select * from customer2");
            ResultSet rst = pstm.executeQuery();
            resp.addHeader("Access-Control-Allow-Origin","*");

            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            while (rst.next()) {
                String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                double salary = rst.getDouble(4);

                CustomerDTO customerDTO = new CustomerDTO(id,name,address,salary);

                JsonObjectBuilder customerObject = Json.createObjectBuilder();
                customerObject.add("id",customerDTO.getId());
                customerObject.add("name",customerDTO.getName());
                customerObject.add("address",customerDTO.getAddress());
                customerObject.add("salary",String.valueOf(customerDTO.getSalary()));

                allCustomers.add(customerObject.build());
            }

            resp.getWriter().print(allCustomers.build());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        double cusSalary = Double.parseDouble(req.getParameter("cusSalary"));

        CustomerDTO customerDTO = new CustomerDTO(cusID,cusName,cusAddress,cusSalary);

        resp.addHeader("Content-Type", "application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("insert into customer2 values(?,?,?,?)");
            pstm.setObject(1, customerDTO.getId());
            pstm.setObject(2, customerDTO.getName());
            pstm.setObject(3, customerDTO.getAddress());
            pstm.setObject(4, customerDTO.getSalary());
            if (pstm.executeUpdate() > 0) {

                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("state", "OK");
                objectBuilder.add("message", "Successfully Added.....");
                objectBuilder.add("Data", " ");
                resp.getWriter().print(objectBuilder.build());
            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject customerObject = reader.readObject();

        String cusId = customerObject.getString("id");
        String cusName = customerObject.getString("name");
        String cusAddress = customerObject.getString("address");
        double cusSalary = Double.parseDouble(customerObject.getString("salary"));

        CustomerDTO customerDTO = new CustomerDTO(cusId,cusName,cusAddress,cusSalary);

        resp.addHeader("Access-Control-Allow-Origin","*");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("update customer2 set name=?,address=?,salary=? where id=?");
            pstm.setObject(4,customerDTO.getId());
            pstm.setObject(1,customerDTO.getName());
            pstm.setObject(2,customerDTO.getAddress());
            pstm.setObject(3,customerDTO.getSalary());
            if (pstm.executeUpdate() > 0){
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("state", "OK");
                objectBuilder.add("message", "Successfully Updated.....");
                objectBuilder.add("Data", " ");
                resp.getWriter().print(objectBuilder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        String id = jsonObject.getString("id");

        System.out.println(id);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            resp.addHeader("Access-Control-Allow-Origin","*");

            PreparedStatement pstm = connection.prepareStatement("delete from customer2 where id=?");
            pstm.setObject(1, id);
            if (pstm.executeUpdate() > 0) {

                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("state", "OK");
                objectBuilder.add("message", "Successfully Deleted.....");
                objectBuilder.add("Data", " ");
                resp.getWriter().print(objectBuilder.build());

            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());

        }

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","DELETE");
        resp.addHeader("Access-Control-Allow-Methods","PUT");
        resp.addHeader("Access-Control-Allow-Headers","content-type");
    }
}
