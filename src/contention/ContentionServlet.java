package contention;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class ContentionServlet extends HttpServlet {
  private static Logger logger = Logger.getLogger(ContentionServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    try {
      String key = req.getParameter("key");
      double timeoutMillis = Double.parseDouble(req.getParameter("timeout"));
      DatastoreServiceConfig config = DatastoreServiceConfig.Builder.withDeadline(timeoutMillis / 1000d);
      DatastoreService ds = DatastoreServiceFactory.getDatastoreService(config);
      Entity entity = new Entity("Foo", key);
      entity.setProperty("last", new Date());
      long t1 = System.nanoTime();
      ds.put(entity);
      long t2 = System.nanoTime();
      String msg =
          "OK " + (t2 - t1) + "ns " + (t2 - t1) / 1000 + "\u00b5s " + (t2 - t1) / 1000 / 1000
              + "ms";
      log(resp, msg);
    } catch (Throwable e) {
      log("Throwable" + e);
      e.printStackTrace(resp.getWriter());
    }

  }

  private void log(HttpServletResponse resp, String msg) throws IOException {
    resp.getWriter().println(msg);
    logger.log(Level.INFO, msg);
    logger.log(Level.FINE, msg);
    logger.log(Level.WARNING, msg);
  }

}
