package com.example.taskmanagerapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), TaskClickInterface, TaskClickDeleteInterface {

    private lateinit var tasksRV: RecyclerView
    private lateinit var addFAB: FloatingActionButton
    private lateinit var searchEditText: EditText
    private lateinit var viewModel: TaskViewModel
    private lateinit var taskRVAdapter: TaskRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasksRV = findViewById(R.id.idRVTasks)
        addFAB = findViewById(R.id.idFABAddTask)
        searchEditText = findViewById(R.id.searchEditText)

        tasksRV.layoutManager = LinearLayoutManager(this)

        taskRVAdapter = TaskRVAdapter(this, this, this)
        tasksRV.adapter = taskRVAdapter

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(TaskViewModel::class.java)

        viewModel.allTasks.observe(this, Observer { list ->
            list?.let {
                taskRVAdapter.updateList(it)
            }
        })

        addFAB.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditTaskActivity::class.java)
            startActivity(intent)
        }

        // Add TextWatcher to the search EditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTasks(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }
        })
    }

    // Filter tasks based on search query
    private fun filterTasks(query: String) {
        viewModel.allTasks.observe(this, Observer { list ->
            list?.let {
                val filteredList = it.filter { task ->
                    task.taskTitle.contains(query, ignoreCase = true)
                }
                taskRVAdapter.updateList(filteredList)
            }
        })
    }

    // TaskClickInterface and TaskClickDeleteInterface methods
    override fun onTaskClick(task: Task) {
        val intent = Intent(this@MainActivity, AddEditTaskActivity::class.java)
        intent.putExtra("taskType", "Edit")
        intent.putExtra("taskTitle", task.taskTitle)
        intent.putExtra("taskDescription", task.taskDescription)
        intent.putExtra("taskPriority", task.taskPriority)
        intent.putExtra("taskDueDate", task.taskDueDate)
        intent.putExtra("taskID", task.id)
        startActivity(intent)
    }

    override fun onDeleteIconClick(task: Task) {
        viewModel.deleteTask(task)
        Toast.makeText(this, "${task.taskTitle} Deleted", Toast.LENGTH_LONG).show()
    }
}
