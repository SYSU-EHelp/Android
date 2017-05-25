package com.example.limin.ehelp.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/05/23.
 */

public class UserBean {

    List<Launch> launch;
    List<Response> responses;

    public class Launch {
        public int type;
        public int id;
        public String title;
        public String date;
        public int num;
        public int finished;
    }

    public class Response {
        public int type;
        public int id;
        public String title;
        public String launcher_username;
        public int num;
        public int finished;
    }
}
