package ar.edu.unq.agiletutor.service

import ar.edu.unq.agiletutor.ItemNotFoundException
import ar.edu.unq.agiletutor.UsernameExistException
import ar.edu.unq.agiletutor.model.Attendance
import ar.edu.unq.agiletutor.model.Student
import ar.edu.unq.agiletutor.persistence.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentService {

    @Autowired
    private lateinit var repository: StudentRepository

    @Transactional
    fun register(student: Student): Student {

        if (existStudent(student)) {
            throw UsernameExistException("Student with email:  ${student.email} is used")
        }

        return repository.save(student)
    }

    @Transactional
    fun findAll(): List<Student> {
        return repository.findAll()
    }

    private fun existStudent(student: Student): Boolean {
        var bool = false
        val students = repository.findAll().toMutableList()
        if (students.isNotEmpty()) {
            bool = students.any { it.email == student.email }
        }
        return bool
    }

    @Transactional
    fun findByID(id: Long): Student {
        val student = repository.findById(id)
        if (!(student.isPresent)) {
            throw ItemNotFoundException("Student with Id:  $id not found")
        }
        return student.get()
    }

    @Transactional
    fun findByName(name: String): List<Student> {
        val students = repository.findAll()
        return students.filter { (it.name == name) } ?: throw ItemNotFoundException("Not found student")
    }

    @Transactional
    fun updateattendances(studentId: Long, attendances: List<AttendanceDTO>): Student {
        val student = findByID(studentId)
        student.attendances = attendances.map { it.aModelo() }.toMutableSet()
        // student.attendancepercentage = calcularPorcentajeDeAsistencias(student.attendances)
        student.calcularPorcentajeDeAsistencias()
        return repository.save(student)
    }

    private fun calcularPorcentajeDeAsistencias(attendances: Set<Attendance>): Double {
        var count = 0.0

        for (attendance in attendances) {
            if (attendance.attended) {
                count++
            }
        }
        return (count * (100 / 6).toDouble())
    }

    @Transactional
    fun update(id: Long, entity: StudentDTO): Student {
        if (!repository.existsById(id)) {
            throw ItemNotFoundException("Student with Id:  $id not found")
        }
        return repository.save(entity.aModelo())
    }

    fun attendancesFromAStudent(id: Long): Set<Attendance> {
        val student = findByID(id)
        return student.attendances
    }

    fun attendancesPercentageFromAStudent(id: Long): Double {
        val student = findByID(id)
        return student.calcularPorcentajeDeAsistencias()
    }

    @Transactional
    fun studentsWirhoutAbsents(): List<Student> {
        return findAll().filter { it.sinFaltas() }
    }

    @Transactional
    fun studentsWithAbsents(): List<Student> {
        return findAll().filter { !it.sinFaltas() }
    }

    @Transactional
    fun attendedDays(id: Long): List<Attendance> {
        val student = findByID(id)
        return student.attended()
    }

    @Transactional
    fun absentdDays(id: Long): List<Attendance> {
        val student = findByID(id)
        return student.absent()
    }

    @Transactional
    fun studentsAttendedAtAParticularDay(day: Int): List<Student> {
        return findAll().filter { it.attendedDay(day) }
    }

    @Transactional
    fun blockOrUnblockAStudent(id: Long, blocked: String): Student {
        var student = findByID(id)
        println(blocked+"blockeado?"+student.toString()+"print"+blocked.toBoolean().toString())
       // student.blocked = blocked.toBoolean()
        student.setBlockedStudent(blocked.toBoolean())
        println(student.blocked.toString()+"resultadoB")
        return repository.save(student)
    }
}