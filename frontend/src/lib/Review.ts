

export interface Review {
    id: string;
    username: string;
    opinion: string;
    overallRagin: string;
    _links: any;
}


async function fetchReviewsByCourse() {}
async function fetchReviewsByTeacher() {}
async function fetchReviewsByUser() {}

export const ReviewService = {
    fetchReviewsByCourse,
    fetchReviewsByTeacher,
    fetchReviewsByUser
}