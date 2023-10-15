package br.com.sertech.todolist.controller;

import br.com.sertech.todolist.model.TaskModel;
import br.com.sertech.todolist.repository.ITaskRepository;
import br.com.sertech.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity createTask(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
        var currenDate = LocalDateTime.now();
        if (currenDate.isAfter(taskModel.getStartAt()) || currenDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A Data de Início / Data Término deve ser maior que a data Atual");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A Data de Início deve ser menor que a data de término!");
        }
        var taskCreated = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(taskCreated);
    }
    @GetMapping("/list")
    public List<TaskModel>list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }
    @PutMapping("/{id}")
    public ResponseEntity update( @PathVariable UUID id, @RequestBody TaskModel taskModel, HttpServletRequest request){
        var task = this.taskRepository.findById(id).orElse(null);
        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada!");
        }

        var idUser = request.getAttribute("idUser");
        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar essa tarefa!");
        }
        Utils.copyNonNUllProperties(taskModel,task);
        var taskupdate =  this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskupdate);
    }

}