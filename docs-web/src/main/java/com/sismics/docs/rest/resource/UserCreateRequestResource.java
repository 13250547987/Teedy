package com.sismics.docs.rest.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.json.*;
import java.util.List;

import com.sismics.docs.core.dao.UserCreateRequestDao;
import com.sismics.docs.core.model.jpa.UserCreateRequest;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.rest.exception.*;
import com.sismics.docs.core.constant.Constants;
import com.sismics.rest.exception.ForbiddenClientException;



/**
 * 用户注册申请资源：游客提交 + 管理员审批
 */
@Path("/user_request")
@PermitAll
public class UserCreateRequestResource extends BaseResource {
    private final UserCreateRequestDao dao = new UserCreateRequestDao();

    /**
     * 游客或登录用户提交注册申请
     */
    @PUT
    @PermitAll
    public Response create(
        @FormParam("username") String username,
        @FormParam("password") String password,
        @FormParam("email") String email,
        @FormParam("storage_quota") String storageQuotaStr
    ) {
        // 1. 校验输入
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        email    = ValidationUtil.validateLength(email, "email", 1, 100);
        ValidationUtil.validateEmail(email, "email");
        Long storageQuota = ValidationUtil.validateLong(storageQuotaStr, "storage_quota");

        // 2. 构造实体
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername(username);
        req.setPassword(password);
        req.setEmail(email);
        req.setStorageQuota(storageQuota);
        req.setRoleId(Constants.DEFAULT_USER_ROLE);


        // 区分发起人：匿名 or 已登录
        String creatorId = principal != null ? principal.getId() : null;

        // 3. 持久化
        try {
            dao.create(req, creatorId);
        } catch (Exception e) {
            throw new ServerException("RequestCreationError", "创建注册申请失败", e);
        }

        // 4. 返回 201 Created + 新申请 ID
        JsonObjectBuilder resp = Json.createObjectBuilder()
            .add("status", "ok")
            .add("request_id", req.getId());
        return Response.status(Response.Status.CREATED)
                       .entity(resp.build())
                       .build();
    }

    /**
     * 2. 管理员查看所有注册申请
     */
    @GET
    public Response list() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // List<UserCreateRequest> list = dao.findAll();
        // 只取 PENDING 的申请
        List<UserCreateRequest> list = dao.findByStatus("PENDING");
        JsonArrayBuilder arr = Json.createArrayBuilder();
        for (UserCreateRequest r : list) {
            JsonObjectBuilder o = Json.createObjectBuilder()
                .add("id",           r.getId())
                .add("username",     r.getUsername())
                .add("email",        r.getEmail())
                .add("storage_quota",r.getStorageQuota())
                .add("status",       r.getStatus())
                .add("create_date",  r.getCreateDate().getTime())
                .add("password",     r.getPassword());
            if (r.getReviewerId() != null) {
                o.add("reviewer_id", r.getReviewerId())
                 .add("review_date", r.getReviewDate().getTime());
            }
            arr.add(o);
        }

        JsonObject resp = Json.createObjectBuilder()
            .add("requests", arr)
            .build();
        return Response.ok(resp).build();
    }

    /**
     * 3. 管理员审批（approve/reject）
     */
    @PUT
    @Path("{id}")
    // @Consumes(MediaType.APPLICATION_JSON)
    public Response process(
        @PathParam("id") String id,
        @FormParam("action")   String action,
        @FormParam("password") String password  // approve 时需要，否则可以忽略
    ) {
        // System.out.println("process: " + id + " " + action + " " + password);
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        UserCreateRequest req = dao.getById(id);
        if (req == null) {
            throw new ClientException("RequestNotFound", "申请不存在");
        }
        if (!"PENDING".equals(req.getStatus())) {
            throw new ClientException("InvalidState", "该申请已被处理");
        }

        try {
            if ("approve".equals(action)) {
                // form 中 password 已经是字符串
                if (password == null || password.length() < 8) {
                    throw new ClientException("InvalidPassword", "需提供至少8位的初始密码");
                }
                dao.approve(req, password, principal.getId());
            } else if ("reject".equals(action)) {
                dao.reject(req, principal.getId());
            } else {
                throw new ClientException("InvalidAction", "不支持的操作：" + action);
            }
        } catch (ClientException ce) {
            throw ce;
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Login already used", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }

        return Response.ok(Json.createObjectBuilder()
                               .add("status", "ok")
                               .build())
                       .build();
    }
}
