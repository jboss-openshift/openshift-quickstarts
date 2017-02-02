/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openshift.quickstarts.undertow.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Doug Palmer
 */
public class PhoneBookServlet extends HttpServlet {

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String TABLE = "phonebook";

    private String username;
    private String password;
    private String url;
    private String driver;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        
        url = config.getInitParameter("url");
        driver = config.getInitParameter("driver");
        username = config.getInitParameter("username");
        password = config.getInitParameter("password");            
        
        try {
            switch (driver) {
                case "mysql":
                    Class.forName("com.mysql.jdbc.Driver");
                    break;
                case "postgresql":
                    Class.forName("org.postgresql.Driver");
                    break;
            }
            String create = "CREATE TABLE IF NOT EXISTS " + TABLE + " (" + NAME + " VARCHAR(20)," + NUMBER +" VARCHAR(10))";
            Connection con = DriverManager.getConnection(url, username, password);
            Statement stmt = con.createStatement();
            stmt.execute(create);
        } catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            PrintWriter writer = resp.getWriter();
            Connection con = DriverManager.getConnection(url, username, password);
            Statement stmt = con.createStatement();

            String name = req.getParameter(NAME);
            if(name == null) {
                throw new ServletException("Parameter " + NAME + " is required");
            }
            String number = req.getParameter(NUMBER);
            if(number == null) {
                String select = "SELECT * FROM " + TABLE + " WHERE " + NAME +"='" + name + "'";
                ResultSet result = stmt.executeQuery(select);
                result.next();
                number = result.getString(NUMBER);
                writer.write("Number for " + name + " is " + number);
            }
            else {
                String insert = "INSERT INTO " + TABLE + " VALUES ('"+ name + "','" + number +"')";
                stmt.execute(insert);
                writer.write("Stored " + number + " for " + name);
            }
            con.close();
            writer.close();
        }
        catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
