from pydantic import BaseModel, EmailStr, Field

class LoginRequest(BaseModel):
    """登录请求"""
    email: EmailStr
    password: str = Field(..., min_length=6)

class RegisterRequest(BaseModel):
    """注册请求"""
    email: EmailStr
    password: str = Field(..., min_length=6)
    username: str = Field(..., min_length=1, max_length=100)

class RefreshTokenRequest(BaseModel):
    """刷新Token请求"""
    refreshToken: str

