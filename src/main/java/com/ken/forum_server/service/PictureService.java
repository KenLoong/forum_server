package com.ken.forum_server.service;

import java.util.List;

public interface PictureService {


    void addPicture(int pid, String fileName);

    List<String> findByPid(Integer id);
}
