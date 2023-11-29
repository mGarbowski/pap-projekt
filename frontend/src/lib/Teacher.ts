import {Course} from "./Course";
import api from "./api";

export interface Teacher {
    id: string,
    name: string,
    _links: any
}

/**
 * Request the data of a teacher
 * @param teacherId - teacher's id
 */
export async function fetchTeacher(teacherId: string): Promise<Teacher> {
    // TODO: teacherId does not exist in the database
    return await api.get(`/teachers/${Number(teacherId)}`)
        .then(response => response.json())
        .catch(e => console.error(e));
}

/**
 * Get the data of all teacher's courses
 */
export async function getTeacherCourses(teacherId: string): Promise<Course[]> {
    const response = await api.get(`/teachers/${Number(teacherId)}/courses`);
    const json = await response.json();
    return json._embedded.courses;
}

export async function fetchTeachers(): Promise<Teacher[]> {
    const response = await api.get("/teachers");
    const json = await response.json();
    return json._embedded.teachers;
}