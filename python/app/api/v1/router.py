from fastapi import APIRouter
from app.api.v1 import auth, inscription, knowledge, interpretation

router = APIRouter()

router.include_router(auth.router)
router.include_router(inscription.router)
router.include_router(knowledge.router)
router.include_router(interpretation.router)

