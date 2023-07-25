package com.udacity.jwdnd.course1.cloudstorage.exception;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionResolver implements HandlerExceptionResolver {
    private UserService userService;
    private FileService fileService;

    public ExceptionResolver(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        // adapted from https://knowledge.udacity.com/questions/341427

        User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        ModelAndView mav = new ModelAndView("home");

        if(e instanceof MaxUploadSizeExceededException){
            mav.setViewName("home.html");
            mav.addObject("alertMessage", "File size exceeded!");
            mav.addObject("alertError", true);
            mav.addObject("files", fileService.getFilesFor(user));
            return mav;
        }
        return mav;
    }
}
