package action.book;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import model.book.BookDAO;
import model.book.BookVO;

public class BookSearchAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String genre = request.getParameter("genre");
		String keyword = request.getParameter("keyword");
		BookDAO dao = BookDAO.getInstance();
		String url = "Book?cmd=search";
		if (request.getParameter("search").equals("all")) { // 통합검색시
			String search = request.getParameter("search");
			String s_query = " like '%" + keyword + "%'";
			String addtag = "&keyword=" + URLEncoder.encode(keyword, "utf-8") + "&search=" + search;

			int nowpage = 1;
			int maxlist = 8; // 웹페이지당 글 수
			int totpage = 1;
			int totcount = dao.bookCount(genre, keyword); // 총 글 수
			System.out.println(totcount);
			// 총페이지수 계산
			if (totcount % maxlist == 0) {
				totpage = totcount / maxlist;
			} else {
				totpage = totcount / maxlist + 1;
			}
			if (totpage == 0)
				totpage = 1;

			if (request.getParameter("page") != null && !request.getParameter("page").equals("")) {
				nowpage = Integer.parseInt(request.getParameter("page"));
			}
			if (nowpage > totpage) {
				nowpage = totpage;
			}

			int pagestart = (nowpage - 1) * maxlist;// 페이지 시작번호(0,10)
			int endpage = nowpage * maxlist;// 페이지 끝번호(10,20)
			int listcount = totcount - ((nowpage - 1) * maxlist) + 1;

			List<BookVO> list = dao.pagingList(s_query, pagestart, endpage);

			for (int i = 0; i < list.size(); i++) { // 검색결과 중복제거
				for (int j = i + 1; j < list.size(); j++) {
					if (list.get(i).getBookName().equals(list.get(j).getBookName())) {
						System.out.println(list.get(j).getBookName());
						list.remove(j);
					}
				}
			}
			request.setAttribute("totcount", totcount);
			request.setAttribute("addtag", addtag);
			request.setAttribute("url", url);
			request.setAttribute("nowpage", nowpage);
			request.setAttribute("totpage", totpage);
			request.setAttribute("listcount", listcount);
			request.setAttribute("list", list);
			request.setAttribute("keyword", keyword);
			// 페이지 스킵 처리
			String pageSkip = util.PageIndex.pageList(nowpage, totpage, url, addtag);
			request.setAttribute("pageSkip", pageSkip);
			request.setAttribute("list", list);
			RequestDispatcher rd = request.getRequestDispatcher("genre/search_list.jsp");
			rd.forward(request, response);
		} else { // 조건검색시
			String search = request.getParameter("search");
			String s_query = search + " like '%" + keyword + "%'";
			String addtag = "&keyword=" + URLEncoder.encode(keyword, "utf-8") + "&search=" + search;
			int nowpage = 1;
			int maxlist = 8; // 웹페이지당 글 수
			int totpage = 1;
			int totcount = dao.bookCount2(s_query); // 총 글 수

			// 총페이지수 계산
			if (totcount % maxlist == 0) {
				totpage = totcount / maxlist;
			} else {
				totpage = totcount / maxlist + 1;
			}
			if (totpage == 0)
				totpage = 1;

			if (request.getParameter("page") != null && !request.getParameter("page").equals("")) {
				nowpage = Integer.parseInt(request.getParameter("page"));
			}
			if (nowpage > totpage) {
				nowpage = totpage;
			}

			int pagestart = (nowpage - 1) * maxlist;// 페이지 시작번호(0,10)
			int endpage = nowpage * maxlist;// 페이지 끝번호(10,20)
			int listcount = totcount - ((nowpage - 1) * maxlist) + 1;

			List<BookVO> list = dao.pagingList2(s_query, pagestart, endpage);

			for (int i = 0; i < list.size(); i++) { // 검색결과 중복제거
				for (int j = i + 1; j < list.size(); j++) {
					if (list.get(i).getBookName().equals(list.get(j).getBookName())) {
						list.remove(j);
					}
				}
			}

			request.setAttribute("totcount", totcount);
			request.setAttribute("addtag", addtag);
			request.setAttribute("url", url);
			request.setAttribute("nowpage", nowpage);
			request.setAttribute("totpage", totpage);
			request.setAttribute("listcount", listcount);
			request.setAttribute("list", list);
			request.setAttribute("keyword", keyword);
			// 페이지 스킵 처리
			String pageSkip = util.PageIndex.pageList(nowpage, totpage, url, addtag);
			request.setAttribute("pageSkip", pageSkip);
			request.setAttribute("list", list);
			RequestDispatcher rd = request.getRequestDispatcher("genre/search_list.jsp");
			rd.forward(request, response);
		}

	}
}
