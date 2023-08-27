package lk.ijse.pos.servlet;

import lk.ijse.pos.dto.ItemDTO;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/pages/item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();

            resp.addHeader("Access-Control-Allow-Origin","*");

            JsonArrayBuilder allItems = Json.createArrayBuilder();
            ItemDTO itemDTO = new ItemDTO();
            while (resultSet.next()){
                itemDTO.setCode(resultSet.getString(1));
                itemDTO.setName(resultSet.getString(2));
                itemDTO.setQty(resultSet.getInt(3));
                itemDTO.setPrice(resultSet.getDouble(4));

                JsonObjectBuilder itemObject = Json.createObjectBuilder();
                itemObject.add("code",itemDTO.getCode());
                itemObject.add("name",itemDTO.getName());
                itemObject.add("qty",String.valueOf(itemDTO.getQty()));
                itemObject.add("price",String.valueOf(itemDTO.getPrice()));

                allItems.add(itemObject.build());
            }

            resp.getWriter().print(allItems.build());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("description");
        int qty = Integer.parseInt(req.getParameter("qty"));
        double price = Double.parseDouble(req.getParameter("unitPrice"));

        ItemDTO itemDTO = new ItemDTO(code,name,qty,price);

        resp.addHeader("Content-Type", "application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO item VALUES (?,?,?,?)");
            pstm.setObject(1,itemDTO.getCode());
            pstm.setObject(2,itemDTO.getName());
            pstm.setObject(3,itemDTO.getQty());
            pstm.setObject(4,itemDTO.getPrice());

            if (pstm.executeUpdate() > 0){
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("state", "OK");
                objectBuilder.add("message", "Successfully Added.....");
                objectBuilder.add("Data", " ");
                resp.getWriter().print(objectBuilder.build());
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject itemObject = reader.readObject();

        String code = itemObject.getString("code");
        String name = itemObject.getString("name");
        int qty = Integer.parseInt(itemObject.getString("qty"));
        double price = Double.parseDouble(itemObject.getString("price"));

        resp.addHeader("Access-Control-Allow-Origin","*");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            ItemDTO itemDTO = new ItemDTO(code,name,qty,price);

            PreparedStatement pstm = connection.prepareStatement("UPDATE item SET name=?, qty=?, price=? WHERE code=?");
            pstm.setObject(4,itemDTO.getCode());
            pstm.setObject(1,itemDTO.getName());
            pstm.setObject(2,itemDTO.getQty());
            pstm.setObject(3,itemDTO.getPrice());

            if (pstm.executeUpdate() > 0){
                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("state","OK");
                response.add("message","Successfully Added.!");
                response.add("Data","");
                resp.getWriter().print(response.build());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        String code = jsonObject.getString("code");

        resp.addHeader("Access-Control-Allow-Origin","*");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            PreparedStatement pst = connection.prepareStatement("DELETE FROM item WHERE code=?");
            pst.setObject(1,code);

            if (pst.executeUpdate() > 0){
                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("state","OK");
                response.add("message","Successfully Added.!");
                response.add("Data","");
                resp.getWriter().print(response.build());
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","PUT,DELETE");
        resp.addHeader("Access-Control-Allow-Headers","content-type");
    }
}

