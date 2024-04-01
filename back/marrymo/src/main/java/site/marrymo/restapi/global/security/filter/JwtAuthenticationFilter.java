package site.marrymo.restapi.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import site.marrymo.restapi.global.jwt.JWTProvider;
import site.marrymo.restapi.global.redis.service.RedisService;
import site.marrymo.restapi.global.jwt.dto.TokenDTO;
import site.marrymo.restapi.user.exception.UserErrorCode;
import site.marrymo.restapi.user.exception.UserException;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JWTProvider jwtProvider;
	private final RedisService redisService;

	//refresh token 만료 기한 30 days
	@Value("${jwt.refresh-token.expiretime}")
	private long refreshTokenExpireTime;

	/**
	 * [요청 시 거치는 필터 로직]
	 * Request는 아래와 같은 로직을 통과한다
	 * 예외) 카카오 로그인을 할 경우나 비회원이 접근을 할 경우 쿠키에 토큰을 담아 넘겨주지 않으므로 해당 url에 대해서는 filter를 거치지 못하도록 한다.
	 * 1. Request에서 쿠키를 가져온 후 accessToken과 refreshToken을 추출한다. (token을 토대로 userCode도 가져온다)
	 *
	 * 2. 프론트에 401 에러를 보내서 로그인 창으로 리다이렉트 시키는 경우
	 * 2-1). access token과 refresh token이 각각 쿠키에 담겨서 넘어와야 하는데(쿠키 2개가 넘어와야 한다) 하나라도 없으면  401 에러을 보낸다.
	 * 2-2). 이미 로그아웃 돼서 만료된 refresh token을 가지고 접근 하려고 한다면 401 에러를 보낸다.
	 * 2-3). access token과 refresh 토큰이 모두 만료 되었을 시 401 에러를 보낸다.
	 *
	 * 3. jwtProvider는 유효하지 않은 토큰이 있다면 다시 생성한 후 map에 담아서 가져온다.
	 * (ex) accessToken만이 유효하지 않았다면 accessToken만 map에 담아서 가져온다.
	 * 4. accessToken이 유효하지 않았다면 기존에 accessToken을 담고 있던 쿠키를 삭제 시키고 다시 발급한 accessToken을 쿠키에 담는다.
	 * 5. HttpServletResponse에 cookie를 담아서 보낸다.
	 *
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		FilterChain filterChain) throws ServletException, IOException {

		log.debug("hihi");
		String requestURI = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getRequestURI();
		log.debug("contextPath={}",contextPath);

		if (requestURI.startsWith("/login") ||
				contextPath.equals("/api/moneygift/send") ||
				containsContextPath(contextPath) ||
				requestURI.equals("/oauth2/authorization/kakao")
		) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}

		String accessToken = "";
		String refreshToken = "";
		String userCode = "";

		// Request에서 쿠키를 가져온 후 accessToken과 refreshToken을 추출
		Cookie[] cookies = httpServletRequest.getCookies();

		//쿠키가 모두 만료되어 없거나
		//하나의 토큰만 하나의 쿠키에 담겨오는 경우
		//exception을 터뜨려 재로그인 하도록 해준다.
		if (cookies == null || cookies.length == 1) {
			removeAllCookies(httpServletResponse, cookies);

			// 에러페이지 만들기
			httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}

		for (Cookie cookie : cookies) {
			String tokenName = cookie.getName();
			String tokenValue = cookie.getValue();

			if (tokenName.equals("accessToken")) {
				accessToken = tokenValue;

				userCode = jwtProvider.getUserCode(accessToken);
			} else if (tokenName.equals("refreshToken")) {
				refreshToken = tokenValue;

				userCode = jwtProvider.getUserCode(refreshToken);
			}
		}

		// 로그아웃 해서 만료된 refresh token을 가지고 접근 할 경우
		// exception 터뜨림
		if (!refreshToken.equals("") && jwtProvider.validateLogoutToken(refreshToken)) {
			removeAllCookies(httpServletResponse, cookies);
			httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}

		Map<String, Object> tokens = jwtProvider.reIssueToken(accessToken, refreshToken, userCode);

		//만료된 토큰이 존재한다면
		if (tokens != null) {
			Cookie accessTokenCookie = null;
			Cookie refreshTokenCookie = null;

			//access token을 보내줬다면
			//access token이 만료 되었다는 의미
			if (tokens.get("accessToken") != null) {
				//기존 accessToken을 담고 있던 쿠키 제거
				removeCookie(httpServletResponse, cookies, "accessToken");

				//발급된 accessToken을 가져온다
				TokenDTO accessTokenDTO = (TokenDTO)tokens.get("accessToken");

				accessTokenCookie = new Cookie("accessToken", accessTokenDTO.getToken());

				accessTokenCookie.setMaxAge(60 * 24 * 24 * 31);
				accessTokenCookie.setPath("/");
				accessTokenCookie.setHttpOnly(true);
				accessTokenCookie.setSecure(true);
				accessTokenCookie.setDomain("marrymo.site");
			}
			//refresh token을 보내줬다면
			//refresh token이 만료 되었다는 의미
			if (tokens.get("refreshToken") != null) {
				//기존 refreshToken을 담고 있던 쿠키 제거
				removeCookie(httpServletResponse, cookies, "refreshToken");

				TokenDTO refreshTokenDTO = (TokenDTO)tokens.get("refreshToken");

				refreshTokenCookie = new Cookie("refreshToken", refreshTokenDTO.getToken());

				refreshTokenCookie.setMaxAge(60 * 24 * 24 * 31);
				refreshTokenCookie.setPath("/");
				refreshTokenCookie.setHttpOnly(true);
				refreshTokenCookie.setSecure(true);
				refreshTokenCookie.setDomain("marrymo.site");
			}

			// accessToken만 만료 되어서
			// accessToken만 재발급
			if (accessTokenCookie != null && refreshTokenCookie == null) {
				httpServletResponse.addCookie(accessTokenCookie);
			}
			// refreshToken만 만료 되어서
			// refreshToken만 재발급
			else if (accessTokenCookie == null && refreshTokenCookie != null) {
				httpServletResponse.addCookie(refreshTokenCookie);
				redisService.setValue(refreshTokenCookie.getValue(),userCode, refreshTokenExpireTime);
			}
			//accessToken, refreshToken 모두 만료 되었을 시에
			//재로그인 하라는 에러메시지를 보낸다
			else if (accessTokenCookie != null && refreshTokenCookie != null) {
				httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);

	}

	//기존에 쿠키를 제거하는 로직
	public void removeCookie(HttpServletResponse httpServletResponse, Cookie[] cookies, String key) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					cookie.setMaxAge(0);
					httpServletResponse.addCookie(cookie);
				}
			}
		}
	}

	//모든 쿠키를 제거하는 로직
	public void removeAllCookies(HttpServletResponse httpServletResponse, Cookie[] cookies) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setMaxAge(0);
				httpServletResponse.addCookie(cookie);
			}
		}
	}

	public boolean containsContextPath(String contextPath){
		String[] split = contextPath.split("/");

		if(split[0].equals("api") && split[1].equals("wish-item")){
			if(split.length == 4 ||
					(split.length == 3 && split[2].length() == 8 && ('a' <= split[2].charAt(0) && split[2].charAt(0) <= 'z'))){
				return true;
			}
		}

		return false;
	}
}
