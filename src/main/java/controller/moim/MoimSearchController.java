package controller.moim;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import data.Moim;
import repository.Moims;

@WebServlet("/moim/search")
public class MoimSearchController extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String[] cates =req.getParameterValues("cate");
		// System.out.println(Arrays.toString(cates));
		
		int p;
		if(req.getParameter("page")==null) {
			p =1;
		}else {
			p = Integer.parseInt(req.getParameter("page"));
		}
		
		
		
		SqlSessionFactory factory = (SqlSessionFactory) req.getServletContext().getAttribute("sqlSessionFactory");
		SqlSession sqlSession = factory.openSession();
		Map<String,Object> map = new HashMap<>();
		
		map.put("a", (p-1)*6+1);
		map.put("b", 6*p);
		
		List<Moim> list = sqlSession.selectList("moims.findSomeByAtoB", map);
		//		List<Moim> list =Moims.findByCate(cates);
		int total = sqlSession.selectOne("moims.countDatas");
				
		sqlSession.close();

		int lastPage = total / 6 +(total % 5 > 0 ? 1: 0);
		
		int last = (int)Math.ceil(p / 5.0)* 5;
		
		int st = last - 4;
		
		last = last > lastPage ? lastPage : last;
		
		
		req.setAttribute("list", list);
		req.setAttribute("start", st);
		req.setAttribute("last", last);		
		
		boolean existPrev = p>= 6;
		boolean existNext = lastPage > last; 
		
		req.setAttribute("existPrev", existPrev);
		req.setAttribute("existNext", existNext);
		
		req.getRequestDispatcher("/WEB-INF/views/moim/search.jsp").forward(req, resp);		
	}
}
