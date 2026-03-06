package com.tripplannerai.util;

import com.tripplannerai.common.exception.comment.NotValidDepthException;
import com.tripplannerai.common.exception.comment.NullPathException;
import com.tripplannerai.entity.comment.Comment;

public class CommentUtil {
    public static int DEPTH = 5;
    public static int NUMBER = 5;

    public static String getNextPath(Comment lastComment,int depth){
        if(depth < 1 || depth > DEPTH) throw new NotValidDepthException("Not Valid Exception");
        if(lastComment == null) return "0".repeat(NUMBER-1) + "1" + "0".repeat(NUMBER*(DEPTH-1));
        String lastPath = lastComment.getPath();
        String extractPath = lastPath.substring(NUMBER*(depth-1),NUMBER*depth);
        int num = toNum(extractPath);
        String toPath = toPath(num);
        if(depth == 1) return toPath + lastPath.substring(NUMBER*depth);
        if(depth == DEPTH) return lastPath.substring(0,NUMBER*(depth-1)) + toPath;
        return lastPath.substring(0,NUMBER*(depth-1)) + toPath + lastPath.substring(NUMBER*depth);
    }

    private static int toNum(String extractPath){
        if(extractPath == null) throw new NullPathException("path cannot be null!!");
        int num = 0;
        for(int i= 0;i<extractPath.length();i++){
            num = num * 10 + (extractPath.charAt(i) - '0');
        }
        return num;
    }
    private static String toPath(int num){
        String toStr = String.valueOf(num);
        int length = toStr.length();
        int addLength = NUMBER - length;
        if(addLength == 0) return toStr;
        return "0".repeat(addLength) + toStr;
    }


}
