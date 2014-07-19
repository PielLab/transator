package servlets;

import com.google.common.io.Files;
import encrypt.Encrypter;
import io.TempDirMaker;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import runner.PKSPredictor;
import validator.SequenceValidator;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 28/6/13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
//@WebServlet("/ValidateInput")
@MultipartConfig
public class ValidateInput extends HttpServlet {

    public ValidateInput() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        /**
         * Get the fasta file from the request, either from the test area or the file that can be uploaded. Check that the
         * fasta is adequate.
         */

        //String seqInput = request.getParameter("sequenceInput");
        String seqInput = null;
        SequenceValidator validator = null;

        if( ServletFileUpload.isMultipartContent(request) ) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            //factory.setSizeThreshold(yourMaxMemorySize);
            File outputRepPath = TempDirMaker.createTempDir();
            factory.setRepository(outputRepPath);

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Set overall request size constraint
            //upload.setSizeMax(yourMaxRequestSize);


            try {
                // Parse the request
                List<FileItem> items = upload.parseRequest(request);

                for (FileItem item : items) {
                    if(item.isFormField()) {
                        String name = item.getFieldName();
                        if(name.equalsIgnoreCase("sequenceInput")) {
                            seqInput = item.getString();
                        }
                    }
                }

                if(seqInput==null || seqInput.trim().length()==0) {
                    //validator = new SequenceValidator(request.getPart("fastaFile").getInputStream());
                    for (FileItem item : items) {
                        if(!item.isFormField() && item.getFieldName().equalsIgnoreCase("fastaFile")) {
                            validator = new SequenceValidator(item.getInputStream(),outputRepPath.getCanonicalPath());
                        }
                    }
                } else {
                    validator = new SequenceValidator(seqInput,outputRepPath.getCanonicalPath());
                }

            } catch (FileUploadException e) {
                throw new RuntimeException("Problems with fasta upload file",e);
            }
        }


        if(validator==null || validator.fail()) {
            request.getSession().setAttribute("errorExp",validator.getError());
            response.sendRedirect("pages/Error.jsp");
        }
        /**
         * Provide the fasta file generated to the runner object and execute.
         */
        PKSPredictor predictor = new PKSPredictor(validator.getFastaPath(),validator.getOutputPath());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(predictor);
        // TODO fire the execution of the image structure generator, which should wait for the results of the previous
        // execution thread.
        /**
         * Redirect to the result page, leaving in the session the identifiers of each sequence provided.
         */
        List<String> identifiers = validator.getSequenceIdentifiers();
        request.getSession().setAttribute("identifers",identifiers);

        Encrypter encrypter = new Encrypter();
        String encrypted = encrypter.encrypt(validator.getOutputPath());
        System.out.println("Encrypted PKSWeb : "+encrypted);
        request.getSession().setAttribute("tmp", URLEncoder.encode(encrypted,"UTF-8"));
        response.sendRedirect("result.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {


    }
}
