package servlets;

import encrypt.Encrypter;
import runner.PKSPredictor;
import validator.SequenceValidator;

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
@WebServlet("/ValidateInput")
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
        String seqInput = request.getParameter("sequenceInput");
        SequenceValidator validator;
        if(seqInput==null) {
            validator = new SequenceValidator(request.getPart("fastaFile").getInputStream());
        } else {
            validator = new SequenceValidator(seqInput.split("\n"));
        }
        if(validator.fail()) {
            request.getSession().setAttribute("errorExp",validator.getError());
            response.sendRedirect("pages/Error.jsp");
        }
        /**
         * Provide the fasta file generated to the runner object and execute.
         */
        PKSPredictor predictor = new PKSPredictor(validator.getFastaPath(),validator.getOutputPath());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(predictor);
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
