using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dashboard.Entities;

namespace Dashboard {
    public class DbManager {

        protected static string ConnectionString = "Server=.\\SQLExpress;Database=legalDB;User Id=myUsername;Password=myPassword;";

        public static List<ResultTableRow> GetData(string tagRegex, string valueRegex) {
            List<ResultTableRow> results = new List<ResultTableRow>();
            string sql = "SELECT nE.Tag nE.[Value], cR.Filename " +
                         "FROM NamedEntity nE " +
                         "INNER JOIN ClassificationResultEntities cre " +
                         " on nE.ID = cre.NEID " +
                         "INNER JOIN ClassificationResult cr " +
                         "on cre.CRID = cr.ID " +
                         "WHERE nE.Tag LIKE '" + tagRegex + "' " +
                         "OR nE.[Value] LIKE '" + valueRegex + "'";
            using (SqlConnection connection = new SqlConnection(ConnectionString))
            using (SqlCommand command = new SqlCommand(sql, connection)) {
                connection.Open();
                using (SqlDataReader reader = command.ExecuteReader()) {
                    while (reader.Read()) {
                        ResultTableRow rtr = new ResultTableRow();

                        rtr.Klaeger = reader["klaeger"].ToString();
                        rtr.Theme = reader["theme"].ToString();
                        rtr.Target = reader["target"].ToString();
                        rtr.Tag = reader["tag"].ToString();
                        results.Add(rtr);
                    }
                }
            }
            return results;
        }

        public static List<ResultTableRow> GetData(string sql) {
            return new List<ResultTableRow>();
        }

        public List<string> LoadPersons() {
            List<string> persons = new List<string>();
            string sql = "SELECT DISTINCT [Value] FROM NamedEntity WHERE Tag = 'PERSON'";
            using (SqlConnection connection = new SqlConnection(ConnectionString))
            using (SqlCommand command = new SqlCommand(sql, connection)) {
                connection.Open();
                using (SqlDataReader reader = command.ExecuteReader()) {
                    while (reader.Read()){
                        persons.Add(reader["Value"].ToString());
                    }
                }
            }
            return persons;
        }

        public List<string> LoadOrganisations() {
            List<string> organisations = new List<string>();
            string sql = "SELECT DISTINCT [Value] FROM NamedEntity WHERE Tag = 'ORGANISATION'";
            using (SqlConnection connection = new SqlConnection(ConnectionString))
            using (SqlCommand command = new SqlCommand(sql, connection)) {
                connection.Open();
                using (SqlDataReader reader = command.ExecuteReader()) {
                    while (reader.Read()) { 
                        organisations.Add(reader["Value"].ToString());
                    }
                }
            }
            return organisations;
        }

        public List<string> LoadLocations() {
            List<string> locations = new List<string>();
            string sql = "SELECT DISTINCT [Value] FROM NamedEntity WHERE Tag = 'LOCATION'";
            using (SqlConnection connection = new SqlConnection(ConnectionString))
            using (SqlCommand command = new SqlCommand(sql, connection)) {
                connection.Open();
                using (SqlDataReader reader = command.ExecuteReader()) {
                    while (reader.Read()) {
                        locations.Add(reader["Value"].ToString());
                    }
                }
            }
            return locations;
        }
    }
}
