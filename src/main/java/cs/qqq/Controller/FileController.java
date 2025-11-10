package cs.qqq.Controller;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Controller
public class FileController {

    @GetMapping("/download")
    public ResponseEntity<byte []> fileDownload(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        //指定要下载的文件根路径
        String dirPath="E:/pic/";
        //创建该文件对象
        File file=new File(dirPath+File.separator+filename);
        //设置相应头
        HttpHeaders headers=new HttpHeaders();
        //通知浏览器以下载的方式打开(下载文件前对文件名进行转码)
        filename=getFilename(request,filename);
        headers.setContentDispositionFormData("attachment",filename);
        //定义以流的形式下载并返回文件数据
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file),headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(e.getMessage().getBytes(),HttpStatus.EXPECTATION_FAILED);
        }
    }

    private String getFilename(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        //IE不同版本User-Agent中出现的关键词
        String[] IEBrowserKeyWords={"MSIE","Trident","Edge"};
        //获取请求头的代理信息
        String userAgent=request.getHeader("User-Agent");
        for(String keyWord:IEBrowserKeyWords){
            if(userAgent.contains(keyWord)){
                //IE内核浏览器，统一为UTF-8编码显示，并对转换的+进行更正
                return URLEncoder.encode(filename,"UTF-8").replace("+"," ");
            }
        }
        //火狐等其它浏览器统一为ISO-8859-1编码
        return new String(filename.getBytes("UTF-8"),"ISO-8859-1");
    }
    @PostMapping("/uploadFile")
    public String uploadFile(MultipartFile[] fileUpload, Model model){
        //默认文件上传成功，并返回状态信息
        String fileName = null;
        model.addAttribute("uploadStatus","上传成功");
        for(MultipartFile file : fileUpload){

            //获取文件名以及后缀名
            fileName=file.getOriginalFilename();
            //重新生成文件名（根据具体情况生成对应文件名）
            fileName= UUID.randomUUID()+"_"+fileName;
            //指定上传文件本地目录，不存在则需要提前创建
            String dirPath="E:/pic/";
            File filepath=new File(dirPath);
            if (!filepath.exists()){
                filepath.mkdirs();
            }
            try {
                file.transferTo(new File(dirPath+fileName));
            } catch (IOException e) {
                e.printStackTrace();
                //上传失败返回失败信息
                model.addAttribute("uploadStatus","上传失败："+e.getMessage());
            }

        }
        return fileName;
    }
    @GetMapping("/downloadFile/{name}")
    public void download(@PathVariable("name") String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File("E:/pic/"+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            int len=0;
            byte[] bytes = new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}