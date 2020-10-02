package com.ken.forum_server.service.impl;

import com.ken.forum_server.dao.PictureDao;
import com.ken.forum_server.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {

    @Autowired
    private PictureDao pictureDao;

    @Override
    public void addPicture(int pid, String fileName) {
        pictureDao.addPicture(pid,fileName);
    }

    @Override
    public List<String> findByPid(Integer id) {
        return pictureDao.findByPid(id);
    }
}
