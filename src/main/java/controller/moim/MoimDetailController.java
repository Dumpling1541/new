package controller.moim;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import data.Attendance;
import data.Moim;
import data.Reply;
import data.User;
import repository.Attendances;
import repository.Moims;
import repository.Users;

@WebServlet("/moim/detail")
public class MoimDetailController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id = req.getParameter("id");
		Moim moim = Moims.findById(id);
		if(moim == null) {
			resp.sendRedirect("/moim/search");
			return;
		}
		req.setAttribute("moim", moim);

		User manager = 	Users.findById(moim.getManagerId());
		req.setAttribute("manager", manager);
		
		List<Attendance> attendances = Attendances.findByMoimId(id);
		
		for(Attendance a : attendances) {
			User found = Users.findById(a.getUserId());
			a.setUserAvatarURL(found.getAvatarURL());
			a.setUserName(found.getName());
		}
		req.setAttribute("attendances", attendances);
		
		//모임 댓글 가져오기 ===============================
		SqlSessionFactory factory = (SqlSessionFactory)req.getServletContext().getAttribute("sqlSessionFactory");
		SqlSession sqlSession = factory.openSession();
		List<Reply> replys =sqlSession.selectList("replys.findByMoimId", id);
		sqlSession.close();
		

		req.setAttribute("replys", replys);
		
		
		User logonUser = (User)req.getSession().getAttribute("logonUser");
		if(logonUser == null) {
			req.setAttribute("status", -1);
		}else {
			//===================
			int status = Attendances.findUserStatusInMoim(id, logonUser.getId());
			req.setAttribute("status", status);
		}
		
		
		// 뷰로 넘기는 작업은 패스
		
		req.getRequestDispatcher("/WEB-INF/views/moim/detail.jsp").forward(req, resp);
	}
}
