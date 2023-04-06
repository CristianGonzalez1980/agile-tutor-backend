package ar.edu.unq.agiletutor.service

import ar.edu.unq.agiletutor.UsernameExistException
import ar.edu.unq.agiletutor.model.Student
import ar.edu.unq.agiletutor.persistence.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentService {

    @Autowired
    private  lateinit var repository: StudentRepository


    @Transactional
    fun register(student: Student): Student {

        if ( existStudent(student) )  {
            throw UsernameExistException("Student with email:  ${student.email} is used")
        }

        val savedStudent = repository.save(student)
        return savedStudent
         }


    @Transactional
    fun findAll(): List<Student> {
        val students =  repository.findAll()
        return students
    }

    private fun existStudent(student:Student): Boolean {
            var bool = false
            val students = repository.findAll().toMutableList()
            if ( students.isNotEmpty() ) {
                bool =  students.any { it.email == student.email }
            }
            return bool
        }
}