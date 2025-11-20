from fastapi import APIRouter, Depends, Query, Path
from typing import Optional
from app.common.response import Result
from app.common.page_result import PageResult
from app.core.dependencies import get_current_user_id
from app.services.knowledge_service import KnowledgeService

router = APIRouter(prefix="/knowledge", tags=["知识库"])

@router.get("/list", response_model=Result[PageResult])
async def get_knowledge_list(
    page: int = Query(0, ge=0, description="页码，从0开始"),
    size: int = Query(10, ge=1, le=100, description="每页大小"),
    keyword: Optional[str] = Query(None, description="搜索关键词"),
    dynasty: Optional[str] = Query(None, description="朝代筛选"),
    category: Optional[str] = Query(None, description="分类筛选")
):
    """获取知识库列表"""
    service = KnowledgeService()
    try:
        result = await service.get_list(page, size, keyword, dynasty, category)
        return Result.success(result)
    finally:
        await service.close()

@router.get("/{id}")
async def get_knowledge_by_id(
    id: int = Path(..., description="知识库ID")
):
    """获取知识库详情"""
    service = KnowledgeService()
    try:
        knowledge = await service.get_by_id(id)
        return Result.success(knowledge)
    finally:
        await service.close()

@router.get("/search")
async def search_knowledge(
    keyword: str = Query(..., description="搜索关键词"),
    dynasty: Optional[str] = Query(None, description="朝代筛选"),
    tags: Optional[str] = Query(None, description="标签筛选")
):
    """搜索知识库"""
    service = KnowledgeService()
    try:
        results = await service.search(keyword, dynasty, tags)
        return Result.success(results)
    finally:
        await service.close()

