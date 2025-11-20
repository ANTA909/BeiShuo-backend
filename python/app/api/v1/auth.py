from fastapi import APIRouter, Depends
from app.common.response import Result
from app.common.result_code import ResultCode
from app.core.security import jwt_util
from app.core.dependencies import get_current_user_id
from app.services.auth_service import AuthService
from app.schemas.request.auth import LoginRequest, RegisterRequest, RefreshTokenRequest
from app.schemas.response.auth import LoginResponse, UserInfoResponse

router = APIRouter(prefix="/auth", tags=["认证"])

@router.post("/register", response_model=Result[LoginResponse])
async def register(request: RegisterRequest):
    """用户注册"""
    service = AuthService()
    try:
        user = await service.register(request.email, request.password, request.username)
        
        # 生成Token
        user_id = user["id"]
        username = user["username"]
        token = jwt_util.generate_token(user_id, username)
        refresh_token = jwt_util.generate_refresh_token(user_id, username)
        
        response_data = {
            "token": token,
            "refreshToken": refresh_token,
            "userInfo": {
                "id": user_id,
                "username": username,
                "email": user["email"]
            }
        }
        
        return Result.success(response_data)
    finally:
        await service.close()

@router.post("/login", response_model=Result[LoginResponse])
async def login(request: LoginRequest):
    """用户登录"""
    service = AuthService()
    try:
        user = await service.login(request.email, request.password)
        
        # 生成Token
        user_id = user["id"]
        username = user["username"]
        token = jwt_util.generate_token(user_id, username)
        refresh_token = jwt_util.generate_refresh_token(user_id, username)
        
        response_data = {
            "token": token,
            "refreshToken": refresh_token,
            "userInfo": {
                "id": user_id,
                "username": username,
                "email": user["email"]
            }
        }
        
        return Result.success(response_data)
    finally:
        await service.close()

@router.post("/logout", response_model=Result[dict])
async def logout():
    """用户登出"""
    # TODO: 实现登出逻辑（如将Token加入黑名单等）
    return Result.success()

@router.get("/info", response_model=Result[UserInfoResponse])
async def get_current_user_info(user_id: int = Depends(get_current_user_id)):
    """获取当前用户信息"""
    service = AuthService()
    try:
        user = await service.get_user_by_id(user_id)
        user_info = {
            "id": user["id"],
            "username": user["username"],
            "email": user["email"]
        }
        return Result.success(user_info)
    finally:
        await service.close()

@router.post("/refresh", response_model=Result[dict])
async def refresh_token(request: RefreshTokenRequest):
    """刷新Token"""
    if not request.refreshToken or not jwt_util.validate_token(request.refreshToken):
        return Result.error(ResultCode.TOKEN_INVALID)
    
    user_id = jwt_util.get_user_id_from_token(request.refreshToken)
    username = jwt_util.get_username_from_token(request.refreshToken)
    
    if not user_id or not username:
        return Result.error(ResultCode.TOKEN_INVALID)
    
    new_token = jwt_util.generate_token(user_id, username)
    new_refresh_token = jwt_util.generate_refresh_token(user_id, username)
    
    return Result.success({
        "token": new_token,
        "refreshToken": new_refresh_token
    })

