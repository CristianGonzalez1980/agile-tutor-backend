package ar.edu.unq.agiletutor.service

import ar.edu.unq.agiletutor.model.Alumno
import ar.edu.unq.agiletutor.model.Asistencia
import ar.edu.unq.agiletutor.model.Notifyer
import ar.edu.unq.agiletutor.persistence.AttendanceRepository
import ar.edu.unq.agiletutor.persistence.NotifyerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component


@Component
class EmailServiceImpl {

    @Autowired
    private lateinit var notifyerRepository: NotifyerRepository

    @Autowired
    private lateinit var attendanceRepository: AttendanceRepository

    @Autowired
    private lateinit var emailSender: JavaMailSender

    fun existsAny(): Boolean {
        return notifyerRepository.findAll().isNotEmpty()
    }

    fun saveNotifyer(notifyer: Notifyer) {
        notifyerRepository.save(notifyer)
    }

    fun getNotifyer(): Notifyer {
        if (!this.existsAny()) {
            var notifyer: Notifyer = Notifyer()
            this.saveNotifyer(notifyer)
        }
        return notifyerRepository.findAll()[0]!!
    }

    fun notifyAllAbsent(day: Int) {
        val notifyer: Notifyer = getNotifyer()
        var attendaceList: List<Asistencia> = attendanceRepository.findByDay(day).filter { it -> !it.attended!! }
        attendaceList.map { it -> it.alumno!! }.forEach { it -> notifyer.addabsent(it) }
        notifyer.getabsent()!!
            .forEach { it -> this.sendSimpleMessage(it.email!!, notifyer.getSubjectEmail(), notifyer.getTextEmail()) }
        notifyer.removeall()
        this.saveNotifyer(notifyer)
    }

    /*
        fun addAbsent(attendacesList: List<Asistencia>) {
            val notifyer: Notifyer = getNotifyer()
            attendacesList.forEach {  it -> println(it)}

            attendacesList.forEach { it -> notifyer.addabsent(it.alumno!!) }
            this.saveNotifyer(notifyer)
        }
    */
    fun changeSubjectText(text: String) {
        val notifyer: Notifyer = getNotifyer()
        notifyer.setSubjectEmail(text)
        this.saveNotifyer(notifyer)
    }

    fun changeBodyText(text: String) {
        val notifyer: Notifyer = getNotifyer()
        notifyer.setTextEmail(text)
        this.saveNotifyer(notifyer)
    }

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.setFrom(to)
        message.setTo(to)
        message.setSubject(subject)
        message.setText(text)
        emailSender.send(message)
    }
}